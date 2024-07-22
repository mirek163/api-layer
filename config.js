module.exports = {
    // $schema: "https://docs.renovatebot.com/renovate-schema.json",
    extends: ["config:recommended", "group:allNonMajor"],
    timezone: "Europe/Berlin",
    schedule: ["after 10am every weekday"],
    printConfig: true,
    dependencyDashboard: true,
    dryRun: "lookup",
    logFile: "renovate.log",
    autodiscoverFilter: ["mirek163/api-layer"],
    //repositories: [ { repository: 'mirek163/api-layer', bumpVersion: true } ],
    //repositories: ["mirek163/api-layer"],
    baseBranches: ["v2.x.x-renovate"], //"v2.x.x-renovate"
    //forkProcessing: true,
    //onboarding: "false",
    //enabledManagers: ['github-actions'],
    //trustLevel: 'high',
    assignees: ["mirek163"]
};

