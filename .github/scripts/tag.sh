#!/usr/bin/env bash

set -euo pipefail

# Usage: ./tag.sh 5.0.3

version=${1:-}
if [[ -z "$version" ]]; then
  echo "Usage: $0 <version> (e.g. 5.0.3)" >&2
  exit 1
fi

# Ensure version looks like X.Y.Z
if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Version must be of the form X.Y.Z (e.g. 5.0.3)" >&2
  exit 1
fi

major=${version%%.*}                    # 5 from 5.0.3
rest=${version#*.}                      # 0.3
minor=${rest%%.*}                       # 0
major_minor="${major}.${minor}"        # 5.0

current_branch=$(git rev-parse --abbrev-ref HEAD)
expected_branch="v${major}"

if [[ "$current_branch" != "$expected_branch" ]]; then
  echo "You must be on branch '$expected_branch' to tag version $version. Current: $current_branch" >&2
  exit 1
fi

echo "Fetching tags from origin..."
git fetch --tags origin

tag_and_push() {
  local tag=$1
  local allow_delete=${2:-false}

  if $allow_delete; then
    if git rev-parse -q --verify "refs/tags/${tag}" >/dev/null; then
      echo "Deleting local tag ${tag}"
      git tag -d "$tag" >/dev/null
    fi
    echo "Deleting remote tag ${tag} (if exists)"
    git push origin ":refs/tags/${tag}" >/dev/null 2>&1 || true
  fi

  echo "Creating tag ${tag}"
  git tag -a "$tag" -m "Release ${tag}"
  echo "Pushing tag ${tag}"
  git push origin "refs/tags/${tag}"
}

# 1) tag & push X.Y.Z
tag_and_push "$version" false

# 2) tag & push X.Y (delete previous)
tag_and_push "$major_minor" true

# 3) tag & push X (delete previous)
tag_and_push "$major" true

# 4) merge current branch into main and push main (prefer fast-forward)
echo "Switching to main to merge ${current_branch}..."
git fetch origin main
git checkout main

# Ensure local main is synced with origin/main, preferring fast-forward
if ! git merge --ff-only origin/main; then
  echo "Fast-forward not possible to sync local main with origin/main; creating a merge commit"
  git merge --no-ff --no-edit origin/main
fi

# Merge the release branch into main, preferring fast-forward
if ! git merge --ff-only "$current_branch"; then
  echo "Fast-forward not possible when merging ${current_branch} into main; creating a merge commit"
  git merge --no-ff --no-edit "$current_branch"
fi

git push origin main

# 5) switch back to vX branch, merge main into it, and push vX (prefer fast-forward)
echo "Switching back to ${current_branch} and merging main..."
git checkout "$current_branch"
git fetch origin main

# Merge main into the release branch, preferring fast-forward
if ! git merge --ff-only origin/main; then
  echo "Fast-forward not possible when merging main into ${current_branch}; creating a merge commit"
  git merge --no-ff --no-edit origin/main
fi

git push origin "$current_branch"

echo "Done."

