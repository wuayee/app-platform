const elsaTest = (() => {
    const testSuites = {};
    const self = {};
    self.display;
    self.onlyError = false;
    self.addTestSuite = name => {
        const tests = {cases: []};
        tests.test = (description, func) => {
            tests.cases.push({description, func});
        };

        tests.info = (message, first) => {
            if (self.onlyError) {
                return;
            }

            console.log(message);
            self.display && (self.display.innerHTML += (first ? "" : "</br>") + "<font color='dimgray'>▷-----" + message + "----- </font></br>");
        }
        tests.assert = (should, actual, description) => {
            if (description === undefined) {
                description = "test value should be " + should;
            }
            if (should === actual) {
                if (!self.onlyError) {
                    console.log(description + " [passed......]");
                    self.display && (self.display.innerHTML += "✔" + description + " [passed......]</br>");
                }
            } else {
                const err = description + " [failed......]";
                const detail = "expected value is: " + should + ", actual value is:" + actual;
                console.error(err);
                self.display && (self.display.innerHTML += "<font color='red'>✘" + err + "</br>" + detail + "</font></br>");
                self.isSuccess = false;
            }
        };

        tests.assertTrue = (actual, description) => {
            tests.assert(true, actual, description);
        };

        tests.assertFalse = (actual, description) => {
            tests.assert(false, actual, description);
        };

        testSuites[name] = tests;
        return tests;
    };
    self.runAll = async () => {
        for (let t in testSuites) {
            await self.run(t);
        }
    };

    self.run = async name => {
        const tests = testSuites[name];
        tests.before && tests.before();
        let sucess = 0, fail = 0;
        self.display && (self.display.innerHTML += "<h4><font color='steelblue'>☼---------------- begin test " + name + "----------------☼</h4>");
        for (let i = 0; i < tests.cases.length; i++) {
            const t = tests.cases[i];
            console.log("test case: " + t.description + " is testing....");
            self.display && (self.display.innerHTML += "<h5>▶ test case: " + t.description + "....</h4>");
            try {
                self.isSuccess = true;
                await t.func();
                if (self.isSuccess) {
                    sucess++;
                } else {
                    fail++;
                }
            } catch (e) {
                console.error(e);
                self.display && (self.display.innerHTML += "<font color='red'>★★★★------------------------system error--------------------------★★★★ </br>" + e + "</font>");
                fail++;
            }

        }
        self.display && (self.display.innerHTML += "<h4> total: " + tests.cases.length + " cases, success: " + sucess + " cases, fail: " + fail + " cases </h5></br>");
    };
    return self;
})();

export {elsaTest};