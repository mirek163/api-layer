module.exports = {
     // "hostRules":[{
     //     matchHost: "api.github.com",
     //     token: process.env.GITHUB_TOKEN
     // }],
    // logFileLevel: 'debug',
    // logLevel: 'debug',
    // $schema: "https://docs.renovatebot.com/renovate-schema.json",
    extends: ["config:recommended", "group:allNonMajor"],
    timezone: "Europe/Berlin",
    schedule: ["after 10am every weekday"],
    printConfig: true,
    dependencyDashboard: true,
    //dryRun: "lookup",
    logFile: "renovate.log",
    autodiscoverFilter: ["mirek163/api-layer/api-layer"],
    //repositories: ["mirek163/api-layer"],
    baseBranches: ["v3.x.x-renovate"], //"v2.x.x-renovate"

    //onboarding: "false",
    //enabledManagers: ['github-actions'],
    //trustLevel: 'high',
    assignees: ["mirek163"]
};

