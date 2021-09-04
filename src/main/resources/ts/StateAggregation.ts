namespace client {
    export class StateAggregation {
        public groupedOutputState: {} = {};

        initGroupedState() {
            this.groupedOutputState = {};
            this.groupedOutputState["high"] = [];
            this.groupedOutputState["low"] = [];
            this.groupedOutputState["disabled"] = [];
            this.groupedOutputState["outside_low"] = [];
            this.groupedOutputState["outside_high"] = [];
        }

        withNewState(state: string) : {}{
            this.initGroupedState();

            var method_char = state.charAt(0);
            if (method_char != 'o') {
                console.error("got wrong output method char");
            } else{
                state = state.substr(1);
            }

            while (state.length > 0) {
                var x = parseInt(state.charAt(0));
                var y = parseInt(state.charAt(1));
                var s = parseInt(state.charAt(2));
                if (s == 0) {
                    this.groupedOutputState["low"].push(x + "" + y);
                } else if (s == 1) {
                    this.groupedOutputState["high"].push(x + "" + y);

                } else if (s == 7) {
                    this.groupedOutputState["disabled"].push(x + "" + y);
                }
                state = state.substr(3);
            }
            return this.groupedOutputState;
        }

    }

    export class OutputAggregation {
        public groupedOutsideState: {} = {};

        init() {
            this.groupedOutsideState["high"] = 0;
            this.groupedOutsideState["low"] = 0;
        }

        withNewState(state: string) : {}{
            this.init();
            var method_char = state.charAt(0);
            if (method_char != 'u') {
                console.error("got wrong output method char");
            }
            state = state.substr(1);

            while (state.length > 0) {
                var s = parseInt(state.charAt(1));
                if (s == 1) {
                    this.groupedOutsideState["high"]++;
                }
                if (s == 0) {
                    this.groupedOutsideState["low"]++;
                }
                state = state.substr(2);
            }
            return this.groupedOutsideState;
        }
    }
}