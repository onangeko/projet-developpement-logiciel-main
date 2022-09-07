<script setup lang="ts">
	import { api } from "@/http-api";
	import { ref } from "vue";
	import router from "@/router";
	import { ImageType } from "@/image";
	import Image from "./Image.vue";

	const props = defineProps<{ id: number }>();
	const selectedAlgo = ref("");
	const algoValue = ref();
	const imageList = ref<ImageType[]>([]);
	const imageAlgoApplied = ref();
	const selectedId = ref(-1);
	getImageList();

	function getImageList() {
		api.getImageList()
			.then((data) => {
				imageList.value = data;
        imageList.value.splice(0, 2);
			})
			.catch((e) => {
				console.log(e.message);
			});
	}

	const algoList = [
		{ name: "Normal (without filter)", value: "normal" },
		{ name: "Color filter", value: "color" },
		{ name: "Increase luminosity", value: "increaseLumi" },
		{ name: "Histogram equalization", value: "histoEga" },
		{ name: "Sobel's filter (contour)", value: "sobel" },
		{ name: "Averaging filter", value: "flouMoy" },
		{ name: "Gaussian filter", value: "flouGauss" },
		{ name: "Pixelation", value: "pixel" },
		{ name: "Threshold", value: "threshold" },
		{ name: "Negative", value: "negative" },
		{ name: "Sepia", value: "sepia" },
		{ name: "Flag", value: "flags" },
		{ name: "Noise", value: "bruit" },
		{ name: "FusionRGB", value: "fusionrgb" },
		{ name: "FusionHSV", value: "fusionhsv" },
		{ name: "Sepia contrast", value: "sepiaContraste" },
		{ name: "Photomaton", value: "photomaton" },
		{ name: "Anid'algo", value: "anid" },
		{ name: "PhotoMacron", value: "photomacron" }
	];

	api.getImage(props.id)
		.then((data: Blob) => {
			const reader = new window.FileReader();
			reader.readAsDataURL(data);
			reader.onload = () => {
				if (reader.result as string) {
					const aElt = document.getElementById("imgDownload");
					if (aElt !== null) {
						aElt.setAttribute("href", reader.result as string);
					}
				}
			};
		})
		.catch((e) => {
			console.log(e.message);
		});

	function applyAlgo(id: number, algo: string, p1 = -1, p2 = -1) {
		if (p1 == -1 && p2 == -1) {
			api.getImageAlgo0(id, algo)
				.then((data: Blob) => {
					const reader = new window.FileReader();
					reader.readAsDataURL(data);
					reader.onload = () => {
						const imgEltt = document.getElementsByTagName("img");
						const imgElt = imgEltt[0];
						if (imgElt !== null && (reader.result as string)) {
							const aElt = document.getElementById("imgDownload");
							imageAlgoApplied.value = reader.result as string;
							if (aElt !== null)
								aElt.setAttribute("href", reader.result as string);
							if (imgElt !== null)
							imgElt.setAttribute("src", reader.result as string);
						}
					};
				})
				.catch((e) => {
					console.log(e.message);
				});
		} else if (p1 != -1 && p2 == -1) {
			api.getImageAlgo1(id, algo, p1)
				.then((data: Blob) => {
					const reader = new window.FileReader();
					reader.readAsDataURL(data);
					reader.onload = () => {
						const imgEltt = document.getElementsByTagName("img");
						const imgElt = imgEltt[0];
						if (imgElt !== null && (reader.result as string)) {
							const aElt = document.getElementById("imgDownload");
							imageAlgoApplied.value = reader.result as string;
							if (aElt !== null)
								aElt.setAttribute("href", reader.result as string);
							if (imgElt !== null)
							imgElt.setAttribute("src", reader.result as string);
						}
					};
				})
				.catch((e) => {
					console.log(e.message);
				});
		} else if (p1 != -1 && p2 != -1) {
			api.getImageAlgo2(id, algo, p1, p2)
				.then((data: Blob) => {
					const reader = new window.FileReader();
					reader.readAsDataURL(data);
					reader.onload = () => {
						const imgEltt = document.getElementsByTagName("img");
						const imgElt = imgEltt[0];
						if (imgElt !== null && (reader.result as string)) {
							const aElt = document.getElementById("imgDownload");
							imageAlgoApplied.value = reader.result as string;
							if (aElt !== null) {
								aElt.setAttribute("href", reader.result as string);
							}
							if (imgElt !== null) {
								imgElt.setAttribute("src", reader.result as string);
							}
						}
					};
				})
				.catch((e) => {
					console.log(e.message);
				});
		}
	}

	var interval = setInterval(function () {});
	clearInterval(interval);
	var force = Number(0);

	function algo() {
		clearInterval(interval);
		if (selectedAlgo.value === "normal") {
			api.getImage(props.id)
				.then((data: Blob) => {
					const reader = new window.FileReader();
					reader.readAsDataURL(data);
					reader.onload = () => {
						const imgEltt = document.getElementsByTagName("img");
						const imgElt = imgEltt[0];
						if (imgElt !== null && (reader.result as string)) {
							const aElt = document.getElementById("imgDownload");
							imageAlgoApplied.value = "";
							if (aElt !== null)
								aElt.setAttribute("href", reader.result as string);
							if (imgElt !== null)
							imgElt.setAttribute("src", reader.result as string);
						}
					};
				})
				.catch((e) => {
					console.log(e.message);
				});
		}

		if (
			selectedAlgo.value === "sobel" ||
			selectedAlgo.value === "negative" ||
			selectedAlgo.value === "sepia"
		) {
			api.getImageAlgo0(props.id, selectedAlgo.value)
				.then((data: Blob) => {
					const reader = new window.FileReader();
					reader.readAsDataURL(data);
					reader.onload = () => {
						const imgEltt = document.getElementsByTagName("img");
						const imgElt = imgEltt[0];
						if (imgElt !== null && (reader.result as string)) {
							const aElt = document.getElementById("imgDownload");
							imageAlgoApplied.value = reader.result as string;
							if (aElt !== null)
								aElt.setAttribute("href", reader.result as string);
							if (imgElt !== null)
								imgElt.setAttribute("src", reader.result as string);
						}
					};
				})
				.catch((e) => {
					console.log(e.message);
				});
		}

		if (selectedAlgo.value === "anid" || selectedAlgo.value === "photomacron") {
			force = Number(0);
			interval = setInterval(function () {
				applyAlgo(props.id, selectedAlgo.value, force);
				force++;
				if (force > 100) {
					clearInterval(interval);
				}
			}, 100);
		}
		algoValue.value = 0;
	}

	function intervalControl() {
		if (interval === null) {
			interval = setInterval(function () {
				applyAlgo(props.id, selectedAlgo.value, force);
				force++;
				if (force > 100) {
					clearInterval(interval);
				}
			}, 100);
		} else {
			clearInterval(interval);
		}
	}

	function resetInterval() {
		force = Number(0);
		if (interval === null) {
			interval = setInterval(function () {
				applyAlgo(props.id, selectedAlgo.value, force);
				force++;
				if (force > 100) {
					clearInterval(interval);
				}
			}, 100);
		}
	}

	function deleteImage() {
		let confirmDelete = confirm(
			"DO YOU REALLY WANT TO DELETE THIS IMAGE ???!!!!"
		);
		if (confirmDelete) {
			api.deleteImage(props.id)
				.then(() => {
					router.push({ name: "home" });
				})
				.catch((e) => {
					console.log(e.message);
				});
		}
	}
</script>

<template>
	<div>
		<div id="img">
			<Image :id="props.id" />
		</div>
		<div>
			<h5>Select a filter to apply</h5>
			<select v-model="selectedAlgo" @change="algo">
				<option selected disabled>Algorithms</option>
				<option
					v-for="algo in algoList"
					:value="algo.value"
					:key="algo.value"
				>
					{{ algo.name }}
				</option>
			</select>
		</div>
		<div id="algo">
			<div v-if="selectedAlgo === 'color'">
				<input
					:id="selectedAlgo"
					type="range"
					min="0"
					max="360"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-else-if="selectedAlgo === 'increaseLumi'">
				<input
					:id="selectedAlgo"
					type="range"
					min="-254"
					max="254"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-else-if="selectedAlgo === 'histoEga'">
				<input
					id="histoS"
					type="radio"
					value="1"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label for="histoS">S</label>
				<input
					id="histoV"
					type="radio"
					value="2"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label for="histoV">V</label>
			</div>
			<div v-else-if="selectedAlgo === 'flouMoy'">
				<input
					:id="selectedAlgo"
					type="number"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
			</div>
			<div v-else-if="selectedAlgo === 'flouGauss'">
				<input
					:id="selectedAlgo"
					type="number"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
			</div>
			<div v-if="selectedAlgo === 'pixel'">
				<input
					:id="selectedAlgo"
					type="range"
					min="1"
					max="50"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'threshold'">
				<input
					:id="selectedAlgo"
					type="range"
					min="0"
					max="256"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'flags'">
				<input
					:id="selectedAlgo + '0'"
					type="radio"
					value="0"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo + '0'">France</label>
				<br />
				<input
					:id="selectedAlgo + '1'"
					type="radio"
					value="1"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo + '1'">Allemagne</label>
				<br />
				<input
					:id="selectedAlgo + '2'"
					type="radio"
					value="2"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo + '2'">Gabon</label>
			</div>
			<div v-if="selectedAlgo === 'bruit'">
				<input
					:id="selectedAlgo"
					type="range"
					min="0"
					max="255"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'fusionrgb'">
				<select v-model="selectedId">
					<option
						v-for="image in imageList"
						:value="image.id"
						:key="image.id"
					>
						{{ image.name }}
					</option>
				</select>
				<div>
					<h5>Opacity</h5>
				</div>
				<input
					:id="selectedAlgo"
					min="0"
					max="100"
					type="range"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, selectedId, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'fusionhsv'">
				<select v-model="selectedId">
					<option
						v-for="image in imageList"
						:value="image.id"
						:key="image.id"
					>
						{{ image.name }}
					</option>
				</select>
				<div>
					<h5>Opacity</h5>
				</div>
				<input
					:id="selectedAlgo"
					min="0"
					max="100"
					type="range"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, selectedId, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'sepiaContraste'">
				<input
					:id="selectedAlgo"
					type="range"
					min="0"
					max="255"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
				<label :for="selectedAlgo">{{ algoValue }}</label>
			</div>
			<div v-if="selectedAlgo === 'photomaton'">
				<input
					:id="selectedAlgo"
					type="number"
					v-model="algoValue"
					@change="applyAlgo(props.id, selectedAlgo, algoValue);"
				/>
			</div>
			<div
				v-if="selectedAlgo === 'anid' || selectedAlgo === 'photomacron'"
			>
				<a class="del" id="stop" @click="intervalControl()">Stop</a>
				<a id="reset" @click="resetInterval()">Reset</a>
			</div>
		</div>
		<br />
		<div class="divBut">
			<a id="imgDownload" download>Download</a>
		</div>
		<br />
		<div class="divBut">
			<a class="del" @click="deleteImage">Delete</a>
		</div>
	</div>
</template>

<style scoped>
	div {
		padding: 5px;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}
	#img {
		padding: 0px;
	}
	h5 {
		margin: 0px;
		margin-bottom: 5px;
	}
	a {
		text-decoration: none;
		outline: none;
		border: none;
		padding: 8px;
		padding-right: 16px;
		padding-left: 16px;
		color: white;
		background-color: rgb(0, 146, 0);
		border: 2px solid rgb(0, 146, 0); /* Green */
		border-radius: 12px;
		margin-top: 24px;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}

	a:hover {
		cursor: pointer;
		background-color: rgb(3, 119, 3);
		border-color: rgb(3, 119, 3);
	}

	.del {
		background-color: rgb(248, 45, 45);
		border-color: rgb(248, 45, 45);
	}

	.del:hover {
		background-color: rgb(214, 40, 40);
		border-color: rgb(214, 40, 40);
	}

	#addImg,
	#reset {
		background-color: rgb(37, 103, 245);
		border-color: rgb(37, 103, 245);
	}

	#addImg:hover,
	#reset:hover {
		background-color: rgb(16, 77, 207);
		border-color: rgb(16, 77, 207);
	}

	.divBut {
		display: inline-block;
		margin-top: 30px;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}

	#stop,
	#reset {
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}

	#stop {
		margin-right: 10px;
	}

	#reset {
		margin-left: 10px;
	}

	.divBut:hover,
	#stop:hover,
	#reset:hover {
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
		transform: scale(1.25);
	}

	#algo {
		margin-top: 12px;
	}

	input[type="radio"] {
		margin-top: 8px;
	}

	#interBut {
		margin-top: 0px;
	}
</style>
