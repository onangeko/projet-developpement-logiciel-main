<script setup lang="ts">
	import { ref } from "vue";
	import { api } from "@/http-api";

	const target = ref<HTMLInputElement>();

	function submitFile() {
		if (
			target.value !== null &&
			target.value !== undefined &&
			target.value.files !== null
		) {
			const file = target.value.files[0];
			if (file === undefined) return;
			console.log(file)
			let formData = new FormData();
			formData.append("file", file);
			api.createImage(formData)
				.then(() => {
					if (target.value !== undefined) target.value.value = "";
				})
				.catch((e) => {
					console.log(e.message);
				});
		}
	}

	function handleFileUpload(event: Event) {
		target.value = event.target as HTMLInputElement;
	}
</script>

<template>
	<div>
		<h3>Upload an image</h3>
		<div id="inputFile">
			<input
				type="file"
				id="file"
				ref="file"
				@change="handleFileUpload"
			/>
		</div>
		<div>
			<button @click="submitFile">Submit</button>
		</div>
	</div>
</template>

<style scoped>
	#inputFile {
		margin-bottom: 10px;
		margin-top: 12px;
	}
	button,
	input[type="file"]::-webkit-file-upload-button {
		outline: none;
		border: none;
		padding: 8px;
		padding-right: 16px;
		padding-left: 16px;
		color: white;
		background-color: #0ea5e9;
		border: 2px solid #0ea5e9; /* Green */
		border-radius: 12px;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}
	button:hover,
	input[type="file"]::-webkit-file-upload-button:hover {
		background-color: #0d93d1;
		cursor: pointer;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 300ms;
	}
	input[type="file"]::-webkit-file-upload-button {
		margin-right: 12px;
	}
</style>
