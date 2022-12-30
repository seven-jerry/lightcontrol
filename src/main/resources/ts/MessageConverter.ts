namespace client {
    export class MessageConverter {
        public static changeMessage(message: string) {
            var obj = {};
            obj["type"] = "CHANGE";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }

        public static disableMessage(message: string) {
            var obj = {};
            obj["type"] = "DISABLE";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }

        public static masterChangeMessage(hosts:string[],command: string) {
            var obj = {};
            obj["type"] = "CHANGE";
            var hostMap = {};
            for(let host of hosts){
                hostMap[host] = command;
            }
            obj["argument"] = hostMap;
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
