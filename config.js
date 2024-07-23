
module.exports = {
    autodiscover: false,
    platform: "github",
    extends: ["config:recommended", "group:allNonMajor", ":switchToGradleLite"],
    timezone: "Europe/Berlin",
    dependencyDashboard: true,
    logFile: "renovate.log",
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: ["mirek163"],
    schedule: ["after 10am every weekday"],
    printConfig: true,
    recreateWhen: "always"
    //dryRun: "lookup",
    //trustLevel: "high"
    //prCreation: "not-pending",
    // retainStalePrs: true
    // ":prConcurrentLimitNone"
};

