include {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../../modules/eks"
}

dependency "vpc" {
  config_path = "../vpc"

  # Nếu chưa chạy apply ở vpc, dùng mock_outputs tạm thời
  mock_outputs = {
    vpc_id              = "vpc-123456"
    public_subnet_ids   = ["subnet-111111", "subnet-222222"]
    private_subnet_ids  = ["subnet-333333", "subnet-444444"]
  }
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