
module.exports = {
    extends: ["group:allNonMajor", "config:recommended"],
    timezone: "Europe/Berlin",
    dependencyDashboard: true,
    logFile: "renovate.log",
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v2.x.x-renovate"],
    assignees: ["mirek163"],
    schedule: ["after 6am every weekday"],
    printConfig: true,
    recreateWhen: "always", // recreates all closed or blocking PRs not just immortals
    prHourlyLimit: 0, // removes rate limit for PR creation per hour
    npmrc: "legacy-peer-deps=true", //for updating lock-files
    npmrcMerge: true //be combined with a "global" npmrc

};

