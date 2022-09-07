<script setup lang="ts">
import { ref } from "vue";
import { api } from "@/http-api";
import router from "@/router";
import { ImageType } from "@/image";
import Image from "./Image.vue";
import $ from "jquery";

const imageList = ref<ImageType[]>([]);
const ballList = [];
var x = 0
x = $(window).height() / 2;
var y = 0
y = $(window).width() - 30;
const ball = 100;

api.getImageList()
	.then((data) => {
		imageList.value = data;
		imageList.value.splice(0, 2);
	})
	.catch((e) => {
		console.log(e.message);
	});

function makeNewPosition() {
	// Get viewport dimensions (remove the dimension of the div)
	var h = 0
	var w = 0
	h = $(window).height() - 200;
	w = $(window).width() - 200;

	var nh = Math.floor(Math.random() * h);
	var nw = Math.floor(Math.random() * w);

	return [nh, nw];
}

function animateDiv(myclass: string) {
	var newq = makeNewPosition();
	const min = 150;
	const max = 300;
	$(myclass).animate({ top: newq[0], left: newq[1] }, 1000, function () {
		animateDiv(myclass);
		const size = Math.random() * (max - min + 1) + min;
	});
}

function getImage(id: string) {
	api.getImage(Number(id))
		.then((data: Blob) => {
			const reader = new window.FileReader();
			reader.readAsDataURL(data);
			reader.onload = () => {
				const imageData = reader.result as string;
				const imgElt = document.getElementById(id);
				if (imgElt !== null)
					imgElt.setAttribute("src", imageData);
				ballList.push(id);
				$(document).ready(function () {
					animateDiv(`#${imgElt.id}`);
				});
			};
		})
		.catch((e) => {
			console.log(e.message);
		});
	return id;
}

function goToUpload() {
	router.push({ name: "upload" });
}

function showImage(id: number) {
	router.push({ name: "image", params: { id: id } });
}
</script>

<template>
	<div>
		<h3>Gallery</h3>
		<div id="gallery" class="place-self-center">
			<img
				v-for="image in imageList"
				:id="getImage(image.id)"
				:key="image.id"
				@click="showImage(image.id)"
				width="200"
				height="200"
			/>
		</div>
	</div>
</template>

<style scoped>
img {
	position: fixed;
	border-radius: 50%;
	transition-property: all;
	transition-timing-function: ease-in-out;
	transition-duration: 150ms;
	padding: 12px;
	cursor: pointer;
}

img:hover {
	transition-property: all;
	transition-timing-function: ease-in-out;
	transition-duration: 300ms;
	transform: scale(1.25);
}

#gallery {
	margin-top: 24px;
}
</style>
