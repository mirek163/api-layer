module.exports = {
    // packageRules: [
    //     {
    //         matchPackagePatterns : ["*"],
    //         updateTypes: ["major"],
    //         enabled: false
    //     }
    // ],
    // packageRules: [
    //     {
    //         matchUpdateTypes: ["major"],
    //         dependencyDashboardApproval: true
    //     }
    // ],
    extends: ["config:recommended", "group:allNonMajor", "config:recommended"],
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

