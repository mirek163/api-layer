module.exports = {
    extends: ["config:recommended"],
    "packageRules": [
        {
            "description": "Group All patch dependency updates",
            "matchPackagePatterns": ["*"],
            "matchUpdateTypes": ["patch"],
            "groupName": "all patch dependencies",
            "groupSlug": "all-patch"
        }
    ],
    minor:
        {
            dependencyDashboardApproval: true
        },
    major:
        {
            dependencyDashboardApproval: true
        },
    dependencyDashboard: true,
    repositories:
        ['mirek163/api-layer'],
    baseBranches:
        ['v2.x.x'],
    assignees:
        [],
    labels:
        ['dependencies'],
    dependencyDashboardLabels:
        ['dependencies'],
    commitMessagePrefix:
        'chore: ',
    printConfig:
        true,
    prHourlyLimit:
        0, // removes rate limit for PR creation per hour
    npmrc:
        'legacy-peer-deps=true', //for updating lock-files
    npmrcMerge:
        true //be combined with a "global" npmrc
}
;

