namespace helloworld {

	export class Demo {
		greet(): void {
			console.log("hello world");
		}
	}

}

var demo = new helloworld.Demo();
demo.greet();