<script setup lang="ts">
	//import { defineProps } from 'vue';
	import { api } from "@/http-api";
	import router from "@/router";

	const props = defineProps<{ id: number }>();

	api.getImage(props.id)
		.then((data: Blob) => {
			const reader = new window.FileReader();
			reader.readAsDataURL(data);
			reader.onload = () => {
				const galleryElt = document.getElementById("gallery");
				if (galleryElt !== null) {
					const aElt = document.createElement("a");
					aElt.onclick = showImage;
					galleryElt.appendChild(aElt);
					const imgElt = document.createElement("img");
					aElt.appendChild(imgElt);
					if (imgElt !== null && (reader.result as string)) {
						imgElt.setAttribute("src", reader.result as string);
					}
				}
			};
		})
		.catch((e) => {
			console.log(e.message);
		});

	function showImage() {
		router.push({ name: "image", params: { id: props.id } });
	}
</script>

<template>
	<figure id="gallery"></figure>
</template>

<style></style>