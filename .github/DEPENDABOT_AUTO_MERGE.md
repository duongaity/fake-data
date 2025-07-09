# Dependabot Auto-Merge Configuration

This repository is configured with automatic merging of Dependabot pull requests when all tests pass.

## How it works

1. **Dependabot creates PRs**: Dependabot automatically creates pull requests for dependency updates based on the schedule in `.github/dependabot.yml`

2. **Auto-merge workflow triggers**: The `dependabot-auto-merge.yml` workflow triggers on:
   - Pull request events (opened, synchronize, reopened)
   - Pull request reviews
   - Check suite completions
   - Status updates

3. **Checks validation**: The workflow waits for all required checks to complete:
   - Build and Release workflow (unit tests, build, Docker image creation)
   - Any other configured status checks

4. **Automatic merge**: If all checks pass, the PR is automatically merged using squash merge

5. **Failure handling**: If checks fail or timeout, a comment is added explaining why the PR wasn't merged

## Configuration

### Dependabot Settings (`.github/dependabot.yml`)
- **Maven dependencies**: Weekly updates on Mondays at 9:00 AM
- **GitHub Actions**: Weekly updates for workflow dependencies
- **Docker**: Weekly updates for Dockerfile base images
- **Auto-merge scope**: Minor and patch updates for both production and development dependencies

### Auto-merge Workflow (`.github/workflows/dependabot-auto-merge.yml`)
- **Trigger**: Only runs for PRs created by `dependabot[bot]`
- **Wait time**: Up to 15 minutes for checks to complete
- **Merge method**: Squash merge with descriptive commit message
- **Permissions**: Requires `contents: write` and `pull-requests: write`

## Security Considerations

1. **Limited scope**: Only runs for Dependabot PRs, not user-created PRs
2. **Test validation**: Requires all existing CI checks to pass
3. **Timeout protection**: Won't wait indefinitely for checks
4. **Failure notifications**: Comments on PRs when auto-merge fails

## Manual Override

If you need to prevent auto-merge for a specific Dependabot PR:
1. Add a `do-not-merge` label to the PR
2. Convert the PR to draft status
3. Close and reopen the PR (this will require manual review)

## Monitoring

Check the Actions tab to monitor auto-merge activity:
- Successful merges will show as completed workflows
- Failed attempts will show error details in the workflow logs
- PR comments provide status updates

## Troubleshooting

### Common Issues

1. **PR not auto-merging**: Check if all required status checks are passing
2. **Workflow not triggering**: Ensure the PR is created by `dependabot[bot]`
3. **Merge conflicts**: Dependabot will automatically rebase, but manual intervention may be needed
4. **Permission errors**: Verify the `GITHUB_TOKEN` has sufficient permissions

### Required Status Checks

The workflow waits for these checks to pass:
- Build and Release workflow (includes unit tests)
- Any additional status checks configured in branch protection rules

To modify which checks are required, update the branch protection rules in the repository settings.