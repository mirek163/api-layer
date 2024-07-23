
module.exports = {
    autodiscover: false,
    platform: "github",
    timezone: "Europe/Berlin",
    dependencyDashboard: true,
    logFile: "renovate.log",
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: ["mirek163"],
    schedule: ["after 10am every weekday"],
    printConfig: true,
    recreateWhen: "always"
    //recreateClosed: true
    //dryRun: "lookup",
    //trustLevel: "high"
    //prCreation: "not-pending",
    // retainStalePrs: true
    // ":prConcurrentLimitNone"
};

