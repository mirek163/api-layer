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
    baseBranches: ["v3.x.x-renovate", "v2.x.x-renovate"],
    dryRun: "lookup",
    logFile: "renovate.log",
    //onboarding: "false",
    //enabledManagers: ['github-actions'],
    //trustLevel: 'high',
    assignees: ["mirek163"]
};
