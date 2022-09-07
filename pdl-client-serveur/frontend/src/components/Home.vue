<script setup lang="ts">
	import { ref } from "vue";
	import router from "@/router";
	import { api } from "@/http-api";
	import { ImageType } from "@/image";

	const selectedId = ref(-1);
	const imageList = ref<ImageType[]>([]);
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

	function showImage() {
		router.push({ name: "image", params: { id: selectedId.value } });
	}

	function goToUpload() {
		router.push({ name: "upload" });
	}
</script>

<template>
	<div>
		<div v-if="imageList.length">
			<h3>Choose an image</h3>
			<div>
				<select v-model="selectedId" @change="showImage">
					<option
						v-for="image in imageList"
						:value="image.id"
						:key="image.id"
					>
						{{ image.name }}
					</option>
				</select>
			</div>
		</div>
		<div v-else>
			<h2>
				Sorry there isn't any image on the server try
				<a id="upload" @click="goToUpload">uploading</a> one
			</h2>
		</div>
		<div id="knob"></div>
	</div>
</template>

<style scoped>
	#upload {
		color: blue;
	}

	#upload:hover {
		cursor: pointer;
	}
	select {
		margin-top: 12px;
	}
</style>
