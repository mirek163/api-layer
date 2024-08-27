module.exports = {
    globalExtends: ["config:recommended"],
    packageRules: [
        {
            //for v.3.x.x branch find all Packages which are patch, slug them and make PR with name "all patch dependencies"
            "matchBaseBranches": ["v3.x.x-renovate"],
            "groupName": "all patch dependencies",
            "groupSlug": "all-patch",
            "matchPackageNames": ["*"],
            "matchUpdateTypes": ["patch"],
        },
        {
            //for v.3.x.x make dashboard approval to all major and minor dependencies updates
            "matchBaseBranches": ["v3.x.x-renovate"],
            //"ignorePresets": ["group:monorepos", "group:recommended"],
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

