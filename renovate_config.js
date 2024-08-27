module.exports = {
    extends: [":dependencyDashboard", ":semanticPrefixFixDepsChoreOthers", ":ignoreModulesAndTests","group:recommended", "replacements:all", "workarounds:all"],
    //extends: ["config:recommended"],
    packageRules: [
        {
            "matchBaseBranches": ["v3.x.x-renovate"],
            "groupName": "all patch dependencies",
            "groupSlug": "all-patch",
            "matchPackageNames": ["*"],
            "matchUpdateTypes": ["patch"],
        },
        {
            "matchBaseBranches": ["v3.x.x-renovate"],
            "matchUpdateTypes": ["major", "minor"],
            "dependencyDashboardApproval": true,
        },
        // {
        //     "matchBaseBranches": ["v2.x.x"],
        //     "groupName": "all non-major dependencies",
        //     "groupSlug": "all-minor-patch",
        //     "matchPackageNames": ["*"],
        //     "matchUpdateTypes": ["minor", "patch"]
        // },
        // {
        //     "matchBaseBranches": ["v2.x.x"],
        //     "matchUpdateTypes": ["major"],
        //     "dependencyDashboardApproval": true,
        // }
    ],
    dependencyDashboard: true,
    repositories: ['mirek163/api-layer'],
    baseBranches: ['v3.x.x-renovate'],
    printConfig: true,
    labels: ['dependencies'],
    dependencyDashboardLabels: ['dependencies'],
    commitMessagePrefix: 'chore: ',
    prHourlyLimit: 0, // removes rate limit for PR creation per hour
    npmrc: 'legacy-peer-deps=true', //for updating lock-files
    npmrcMerge: true //be combined with a "global" npmrc
};

