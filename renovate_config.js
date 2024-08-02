module.exports = {
    extends: ["config:recommended", "group:allNonMajor"],
    dependencyDashboard: true,
    repositories:
        ['mirek163/api-layer'],
    baseBranches:
        ['v3.x.x-renovate','v2.x.x'],
    assignees:
        [],
    labels:
        ['dependencies'],
    commitMessagePrefix:
        'chore: ',
    printConfig:
        true,
    prHourlyLimit:
        0, // removes rate limit for PR creation per hour
    prConcurrentLimit:
        0, //removes a maximum limit of x concurrent branches/PRs
    npmrc:
        'legacy-peer-deps=true', //for updating lock-files
    npmrcMerge:
        true //be combined with a "global" npmrc
}
;

