#!/bin/bash

set -ue

if [ ! -e docs/antora.yml ]; then
    echo "Run the script from the project root"
    exit 1
fi

release_notes=docs/modules/ROOT/pages/release-notes.adoc
branchname=release
latest_version_from_release_notes=$(grep '^==' ${release_notes} | cut -d' ' -f3)

git tag -d snapshot
git fetch --tags
echo "Current tags:"
git --no-pager tag
echo
read -p "Version [${latest_version_from_release_notes}]: " version
version=${version:-${latest_version_from_release_notes}}

grep "${version}" "${release_notes}" &>/dev/null
if [ $? != 0 ]; then
    echo "Version ${version} not found in release notes, stopping"
    exit 1
fi

echo Releasing version $version
tag="v${version}"
git checkout -b $branchname

# Generate documentation images. The generated images that are not present in
# the normal source repository (because during a snapshot build they are
# generated) need to be actually present in the source tree in the tagged
# release commit, because this is what Antora will check out at a later time in
# order to build the respective module version. So generate them, remove the
# corresponding entry from .gitignore and commit them.
./gradlew generateImages
sed -i -e '/docs\/modules\/ROOT\/assets\/images/d' .gitignore
git add .gitignore
git add docs/modules/ROOT/assets/images/*.svg

# Set versions
sed -i -e 's/version: snapshot/version: '$version'/g' docs/antora.yml
sed -i -e 's/page-component-version: snapshot/page-component-version: '$tag'/g' docs/antora.yml
sed -i -e 's/release-version: snapshot/release-version: '$version'/g' docs/antora.yml
sed -i -e 's/snapshot/'$version'/g' gradle.properties
git add docs/antora.yml
git add gradle.properties

git commit -m "Release version $version"
git tag ${tag}
echo Pushing branch
git push -u origin ${branchname}
echo Pushing tag ${tag}
git push origin ${tag}
git checkout master
