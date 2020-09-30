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
for file in docs/antora.yml gradle.properties; do
    sed -i -e 's/snapshot/'$version'/g' ${file}
    git add $file
done
git commit -m "Release version $version"
git tag ${tag}
git checkout master
git branch -D $branchname
echo Pushing tag ${tag}
git push origin ${tag}

