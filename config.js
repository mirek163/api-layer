
module.exports = {
    autodiscover: false,
    platform: "github",
    onboardingConfig: { "extends": ["config:recommended", "group:allNonMajor"] }, //":switchToGradleLite"
//    extends: ["config:recommended", "group:allNonMajor", ":switchToGradleLite"],
    timezone: "Europe/Berlin",
    dependencyDashboard: true,
    logFile: "renovate.log",
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: ["mirek163"],
    schedule: ["after 8am every weekday"],
    printConfig: true,
    //recreateWhen: "always",
    prHourlyLimit: 0
    //recreateClosed: true
    //dryRun: "lookup",
    //trustLevel: "high"
    //prCreation: "not-pending",
    // retainStalePrs: true
    // ":prConcurrentLimitNone"
};

