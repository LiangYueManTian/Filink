FROM nginx
COPY ./dist /usr/share/nginx/
COPY nginx-132.conf /etc/nginx/nginx.conf
COPY 2120455_witstest.fi-link.net.pem /ssl/2120455_witstest.fi-link.net.pem
COPY 2120455_witstest.fi-link.net.key /ssl/2120455_witstest.fi-link.net.key

# 生产环境专用
#COPY nginx-pro.conf /etc/nginx/nginx.conf
#COPY 2155996_fiberhome.fi-link.net.key /ssl/2155996_fiberhome.fi-link.net.key
#COPY 2155996_fiberhome.fi-link.net.pem /ssl/2155996_fiberhome.fi-link.net.pem
EXPOSE 443
#CMD ["nginx","-g","daemon off"]
