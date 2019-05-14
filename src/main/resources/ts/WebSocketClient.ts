namespace client {
    export interface IWebSocketConsumer {
        handleWebSocketMessage(message: string);

        handleWebSocketError(message: string);

        webSocketHasStarted(): void;

        webSocketHasEnded(): void;
    }

    export class WebSocketClient {
        websocket: WebSocket;
        started: boolean;
        consumer: IWebSocketConsumer;


        constructor(host: string, consumer: IWebSocketConsumer) {
            this.websocket = new WebSocket(host + "/webSocket");
            var that = this;

            this.websocket.onopen = (evt) => {
                that.onSocketOpen(evt);
            };
            this.websocket.onclose = (evt) => {
                that.onSocketClose(evt);
            };
            this.websocket.onmessage = (evt) => {
                that.onSocketMessage(evt);
            };
            this.websocket.onerror = (evt) => {
                that.onSocketError(evt);
            };
            this.consumer = consumer;
        }

        onSocketOpen(evt: Event) {
            console.log(evt);
            this.started = true;
            this.consumer.webSocketHasStarted();
        }


        onSocketClose(evt: Event) {
            this.started = false;
            this.consumer.webSocketHasEnded();
        };

        onSocketMessage(evt: any) {
            this.consumer.handleWebSocketMessage(evt.data);
        }

        onSocketError(evt) {
            this.consumer.handleWebSocketError(evt.data);
        };

        send(message: string) {
            this.websocket.send(message);
        }
    }
}