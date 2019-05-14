namespace client {
    export class MessageConverter {
        public static changeMessage(message: string) {
            var obj = {};
            obj["type"] = "CHANGE";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }

        public static fetchMessage(message: []) {
            var obj = {};
            obj["type"] = "FETCH";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }
    }
}