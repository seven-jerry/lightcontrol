namespace client {
    export class SerialSource {
        baundRate: number;
        port: string;
        serialSources: string;

        public static fromObject(obj: any): SerialSource {
            let source = new SerialSource();
            source.baundRate = obj.baundRate;
            source.port = obj.port;
            source.serialSources = obj.serialSources;
            return source;
        }
    }
}
