namespace client {
    export class Input {
        type: string;
        sourceId: string;
        id: number;
        name: string;
        order: number;
        state: number;

        static fromObject(obj: any) {
            let input = new Input();
            input.type = obj.type;
            input.sourceId = obj.sourceId;
            input.id = obj.id;
            input.name = obj.name;
            input.order = obj.order;
            input.state = obj.state;
            return input;
        }
    }
}

