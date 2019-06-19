# login.sh usuario password
curl -s --data "grant_type=password&client_id=cliente-tres&username=$1&password=$2" http://localhost:8282/auth/realms/sso/protocol/openid-connect/token
