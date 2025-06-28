# README

## 1CICD 

## 2. Argo CD

ArgoCD (Argo Continuous Delivery) là một công cụ triển khai GitOps cho Kubernetes.

![Argo CD](./resources/argocd.png)

Giúp bạn triển khai và quản lý trạng thái của ứng dụng Kubernetes một cách tự động, thông qua các file YAML được lưu trữ trong Git.

> ArgoCD giúp tự động triển khai ứng dụng từ Git lên Kubernetes. Nếu file trong Git thay đổi, ArgoCD sẽ phát hiện và đồng bộ cụm Kubernetes để khớp với trạng thái đó.

![Argo CD](https://github.com/argoproj/argo-cd/raw/master/docs/assets/argocd-ui.gif)

Khi nào nên dùng ArgoCD?
- Bạn muốn triển khai ứng dụng theo GitOps (mọi thứ lưu trong Git).
- Bạn có nhiều môi trường (dev, staging, prod) và muốn tách biệt & dễ kiểm soát.
- Bạn triển khai bằng Helm, Kustomize, plain YAML hoặc mix nhiều công cụ.

Ví dụ thực tế:

Bạn có repo Git chứa file cấu hình K8s:

```yaml
deployment.yaml
service.yaml
ingress.yaml
```

Khi bạn cập nhật phiên bản image trong deployment.yaml từ:

```yaml
image: myapp:v1
```

sang:

```yaml
image: myapp:v2
```

> ArgoCD sẽ phát hiện thay đổi và tự động cập nhật Deployment trên cụm Kubernetes cho bạn.

### Cài đặt Argo CD trên Kubernetes (EKS)

Bước 1: Tạo namespace cho Argo CD

```bash
kubectl create namespace argocd
```

Bước 2: Cài đặt Argo CD bằng manifest

```bash
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

Bước 3: Kiểm tra trạng thái triển khai

```bash
kubectl get pods -n argocd
kubectl get svc -n argocd
```

Bước 4: Truy cập Argo CD UI

Mặc định, argocd-server được triển khai dưới dạng ClusterIP. Để truy cập từ bên ngoài, bạn có thể đổi sang LoadBalancer:

```bash
kubectl port-forward svc/argocd-server -n argocd 8888:443

kubectl patch svc argocd-server -n argocd -p \
  '{"spec": {"type": "LoadBalancer"}}'
```

URL: https://localhost:8888

Sau đó kiểm tra IP:

```bash
kubectl get svc argocd-server -n argocd
```

Bước 5: Lấy mật khẩu đăng nhập

Lấy mật khẩu mặc định:

```bash
kubectl get secret argocd-initial-admin-secret -n argocd \
  -o jsonpath="{.data.password}" | base64 -d && echo
```

Trong đó:
- Username: admin
- Password: (kết quả dòng trên)
- URL: http://<external-ip> (hoặc domain nếu có)

Tham Khảo:
- [Trang chủ ArgoCD](https://argo-cd.readthedocs.io/en/stable/)
- [GitHub chính thức của ArgoCD](https://github.com/argoproj/argo-cd)


Setup Instructions

```bash
# Deploy the backend resources first:
cd nonprod/backend
terragrunt init
terragrunt apply

# Deploy the VPC resources:
cd nonprod/vpc
terragrunt init
terragrunt apply

# Deploy the EKS resources:
cd nonprod/eks
terragrunt init
terragrunt apply
```

## EKS

Lệnh kiểm tra EKS cluster

Kiểm tra trạng thái EKS Cluster

```bash
aws eks describe-cluster --name nonprod-eks-cluster --region us-east-1
``

Kiểm tra Node Group

```bash
aws eks describe-nodegroup --cluster-name nonprod-eks-cluster --nodegroup-name nonprod-node-group --region us-east-1
``

Cấu hình kubectl để truy cập Cluster

```bash
aws eks update-kubeconfig --name nonprod-eks-cluster --region us-east-1
``

Kiểm tra Nodes

```bash
kubectl get nodes
``

Kiểm tra Pods

```bash
kubectl get pods --all-namespaces
``

Cập nhật aws-auth ConfigMap (If Needed)

```bash
kubectl edit configmap aws-auth -n kube-system
```

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: aws-auth
  namespace: kube-system
data:
  mapRoles: |
    - rolearn: arn:aws:iam::YOUR_AWS_ACCOUNT_ID:role/nonprod-eks-node-role
      username: system:node:{{EC2PrivateDNSName}}
      groups:
        - system:bootstrappers
        - system:nodes
  mapUsers: |
    - userarn: arn:aws:iam::YOUR_AWS_ACCOUNT_ID:user/your-user
      username: your-user
      groups:
        - system:masters
```

Apply ArgoCD Service Account:

```bash
kubectl apply -f nonprod/eks/argocd-service-account.yaml
kubectl patch deployment argocd-server -n argocd -p '{"spec":{"template":{"spec":{"serviceAccountName":"argocd-server"}}}}'
```