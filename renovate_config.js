module.exports = {

    extends: ["group:allNonMajor", "config:base"],
    dependencyDashboard: true,
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: [],
    labels: ["dependencies"],
    commitMessagePrefix:"chore: " ,
    printConfig: true,
    prHourlyLimit: 0, // removes rate limit for PR creation per hour
    npmrc: "legacy-peer-deps=true", //for updating lock-files
    npmrcMerge: true //be combined with a "global" npmrc
};

