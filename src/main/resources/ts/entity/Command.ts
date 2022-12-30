namespace client {
    export class Command {
        label: string;
        id: number;
        command: string;
        payload:string;
        order: number;

        public static fromObject(obj: any) {
            let command = new Command();
            command.label = obj.label;
            command.id = obj.id;
            command.command = obj.command;
            command.order = obj.order;
            command.payload = obj.payload;
            return command;
        }

        public static arrayFromObject(obj: any) {
            let build = [];
            for (let commandObj of obj) {
                let command = Command.fromObject(commandObj);
                build.push(command);
            }
            return build;
        }


    }
}
