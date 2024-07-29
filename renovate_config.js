module.exports = {
    extends: ["group:allNonMajor", "config:recommended"],
    dependencyDashboard: true,
    repositories: ['mirek163/api-layer'],
    baseBranches: ["v3.x.x-renovate"],
    assignees: [],
    printConfig: true,
    recreateWhen: "always", // recreates all closed or blocking PRs not just immortals
    prHourlyLimit: 0, // removes rate limit for PR creation per hour
    npmrc: "legacy-peer-deps=true", //for updating lock-files
    npmrcMerge: true //be combined with a "global" npmrc
};

