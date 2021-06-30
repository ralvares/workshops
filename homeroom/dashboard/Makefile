build: ## Build the release and develoment container. The development
	podman build --no-cache -t quay.io/ralvares/workshops:dashboard -f Dockerfile

push: ## Publish release
	podman push quay.io/ralvares/workshops:dashboard
