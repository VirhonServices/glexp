CREATE ROLE glexp WITH LOGIN CREATEDB ENCRYPTED PASSWORD 'glexp';
alter role glexp superuser;
CREATE EXTENSION btree_gist;
