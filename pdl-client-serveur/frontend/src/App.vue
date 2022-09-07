<script setup lang="ts">
</script>

<template>
	<div id="app">
		<nav>
			<ul>
				<li>
					<router-link to="/">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							width="32"
							height="32"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
							class="feather feather-home"
						>
							<path
								d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"
							></path>
							<polyline points="9 22 9 12 15 12 15 22"></polyline>
						</svg>
					</router-link>
				</li>
				<li>
					<router-link to="/gallery">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							width="32"
							height="32"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
							class="feather feather-image"
						>
							<rect
								x="3"
								y="3"
								width="18"
								height="18"
								rx="2"
								ry="2"
							></rect>
							<circle cx="8.5" cy="8.5" r="1.5"></circle>
							<polyline points="21 15 16 10 5 21"></polyline>
						</svg>
					</router-link>
				</li>
				<li>
					<router-link to="/upload">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							width="32"
							height="32"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
							class="feather feather-upload"
						>
							<path
								d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"
							></path>
							<polyline points="17 8 12 3 7 8"></polyline>
							<line x1="12" y1="3" x2="12" y2="15"></line>
						</svg>
					</router-link>
				</li>
			</ul>
		</nav>

		<div>
			<h1 id="title">PDL - L3</h1>
			<router-view />
		</div>
	</div>
</template>

<style>
	@import url("https://fonts.googleapis.com/css2?family=Roboto:wght@400&display=swap");
	* {
		margin: 0px;
		font-family: "Roboto", sans-serif;
	}

	#app {
		text-align: center;
		color: #2c3e50;
		margin-bottom: 20px;
	}
	nav {
		margin-bottom: 24px;
	}

	ul {
		list-style-type: none;
		margin: 0;
		padding: 0;
		overflow: hidden;
		background-color: #333;
	}
	#title {
		margin-bottom: 12px;
	}
	li {
		float: left;
	}
	li a {
		display: block;
		color: white;
		text-align: center;
		padding: 14px 16px;
		text-decoration: none;
	}
	.error {
		color: red;
		font-weight: bold;
		list-style-type: none;
		margin-top: 1em;
	}

	svg {
		border-radius: 50%;
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 150ms;
	}

	svg:hover {
		transition-property: all;
		transition-timing-function: ease-in-out;
		transition-duration: 150ms;
		transform: scale(1.25);
	}
</style>
