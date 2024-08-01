module.exports = {
    extends: ["config:recommended"],
    packageRules: [
        {

            groupName: 'all non-major dependencies',
            matchUpdateTypes: ['minor', 'patch']
        },
        {
            excludePackagePatterns: ['*'],
            matchUpdateTypes: ['major']
        }
        // {
        //     groupName: "all non-major dependencies",
        //     groupSlug: "all-minor-patch",
        //     matchPackageNames: ["*"],
        //     matchUpdateTypes: ["minor", "patch", "digest"]
        // }
        // {
        //     groupName: "eslint",
        //     matchPackagePatterns: ["^eslint", "^@eslint"]
        // }
    ],
//extends: ["config:recommended", "group:allNonMajor"],
//ignorePresets: ["group:monorepos", "group:recommended"],
    dependencyDashboard: true,
    repositories:
        ['mirek163/api-layer'],
    baseBranches:
        ['v3.x.x-renovate'],
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
    npmrc:
        'legacy-peer-deps=true', //for updating lock-files
    npmrcMerge:
        true //be combined with a "global" npmrc
}
;

