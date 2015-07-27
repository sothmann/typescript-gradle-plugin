namespace helloworld {

	export class Demo extends Base {
		greet(): void {
			super.greet();
			console.log("hello world");
		}
	}

}

var demo = new helloworld.Demo();
demo.greet();