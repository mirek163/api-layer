module.exports = {
    extends: ["config:recommended"],
    packageRules: [
        {
            "description": "Group All patch dependency updates",
            "matchPackagePatterns": ["*"],
            "matchUpdateTypes": ["patch"],
            "groupName": "all patch dependencies",
            "groupSlug": "all-patch"
        }
    ],
    major:
        {
            dependencyDashboardApproval: true
        },
    minor:
        {
            dependencyDashboardApproval: true
        },
    hostRules: [{
            matchHost: 'https://repo.spring.io/libs-milestone',
            enabled: false
        }],
    dependencyDashboard: true,
    repositories:
        ['mirek163/api-layer'],
    baseBranches:
        ['v3.x.x-renovate'],
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

