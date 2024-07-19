module.exports = {
    extends: ["config:recommended", "group:allNonMajor"],
    timezone: "Europe/Berlin",
    schedule: ["after 10am every weekday", "every weekend"],
    labels: ["renovate"],
    printConfig: true,
    dependencyDashboard: true,
    baseBranches: ["v3.x.x-renovate", "v2.x.x-renovate"],
    dryRun: "lookup",
    assignees: ["mirek163"]
};
