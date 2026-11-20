// The browser tests (js and wasmJs) run in one headless Chrome on CI runners that build other
// targets at the same time; Mocha's default 2 s per test is too tight for the tests that walk
// every supported day of the lunar calendar there.
config.set({
    client: {
        mocha: {
            timeout: 20000,
        },
    },
});
