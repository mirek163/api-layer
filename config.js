
module.exports = {

    extends: ["config:recommended", "group:allNonMajor"],
    timezone: "Europe/Berlin",
    schedule: ["after 10am every weekday"],
    labels: ["renovate"],
    printConfig: true,
    dependencyDashboard: true,
    //dryRun: "lookup",
    logFile: "renovate.log",
    repositories: [ { repository: 'mirek163/api-layer' } ],
    baseBranches: ["v3.x.x-renovate"],
    assignees: ["mirek163"],
    prCreation: "not-pending",
    automerge: true,
    retainStalePrs: true
};

