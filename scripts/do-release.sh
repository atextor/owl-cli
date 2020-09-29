#!/bin/bash

if [ ! -e docs/antora.yml ]; then
    echo "Run the script from the project root"
    exit 1
fi

release_notes=docs/modules/ROOT/pages/release-notes.adoc
branchname=release
latest_version_from_release_notes=$(grep '^==' ${release_notes} | cut -d' ' -f3)
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
git checkout --orphan $branchname
sed -i -e 's/snapshot/'$version'/g' docs/antora.yml
git add docs/antora.yml
sed -i -e 's/snapshot/'$version'/g' gradle.properties
git add gradle.properties
git commit -m "Release version $version"
git tag ${tag}
git checkout master
git branch -D $branchname
echo Pushing tag ${tag}
git push origin ${tag}

