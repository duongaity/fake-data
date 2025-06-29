include {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../../modules/eks"
}

dependency "vpc" {
  config_path = "../vpc"
}

dependency "backend" {
  config_path = "../backend"
}

inputs = {
  environment          = "nonprod"
  vpc_id               = dependency.vpc.outputs.vpc_id
  public_subnet_ids    = dependency.vpc.outputs.public_subnet_ids
  private_subnet_ids   = dependency.vpc.outputs.private_subnet_ids
  node_desired_size    = 1
  node_max_size        = 3
  node_min_size        = 1
  instance_type        = "t3.medium"
  aws_account_id       = "674059452637"
}