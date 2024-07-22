
module.exports = {
    // $schema: "https://docs.renovatebot.com/renovate-schema.json",
    //hostRules:[{
    //    matchHost: "api.github.com",
    //    token: process.env.RENOVATE_TOKEN
    //}],
    extends: ["config:recommended"],

    //autodiscoverFilter: ["mirek163/api-layer"],
    repositories: [ { repository: 'mirek163/api-layer', bumpVersion: true } ],
    //repositories: ["mirek163/api-layer"],
    baseBranches: ["v3.x.x-renovate"], //"v2.x.x-renovate"
    //onboarding: "true",
    //forkProcessing: true,
    //enabledManagers: ['github-actions'],
    //trustLevel: 'high',
};

