
module.exports = {
    autodiscover: false,
    platform: "github",
    extends: ["config:recommended", "group:allNonMajor"],
    timezone: "Europe/Berlin",
    //schedule: ["after 10am every weekday"],
    //printConfig: true,
    dependencyDashboard: true,
    //dryRun: "lookup",
    logFile: "renovate.log",
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: ["mirek163"],
    recreateWhen: "always",
    trustLevel: "high"

  //  prCreation: "not-pending",
   // retainStalePrs: true
};

