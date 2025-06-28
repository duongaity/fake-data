include {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../modules/ecr"
}

dependency "eks" {
  config_path = "../eks"
}

inputs = {
  environment          = "nonprod"
  repository_name      = "fake-data"
  image_tag_mutability = "MUTABLE"
  scan_on_push         = true
  aws_account_id       = "674059452637"
}