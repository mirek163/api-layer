
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
    recreateWhen: "always",
    prHourlyLimit: 0,
    npmrcMerge: true,
    npmrc: "legacy-peer-deps=true" //for updating lock-files
};

