include {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../../modules/ecr"
}

dependency "eks" {
  config_path = "../eks"

  # Nếu chưa chạy apply ở eks, dùng mock_outputs tạm thời
  mock_outputs = {
    cluster_name     = "mock-cluster"
    cluster_endpoint = "https://mock.eks.amazonaws.com"
    node_group_name  = "mock-node-group"
  }
}

inputs = {
  environment          = "nonprod"
  repository_name      = "fake-data"
  image_tag_mutability = "MUTABLE"
  scan_on_push         = true
  aws_account_id       = "674059452637"
}