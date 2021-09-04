namespace client {
    export class Command {
        label: string;
        id: number;
        command: string;
        order: number;

        public static fromObject(obj: any) {
            let command = new Command();
            command.label = obj.label;
            command.id = obj.id;
            command.command = obj.command;
            command.order = obj.order;
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