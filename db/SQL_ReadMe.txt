Database was built using MySQL


mysql -u root -p root
use swarm;
select * from sprites;
insert into sprites (id, x_coord, y_coord) values ('p1',5,5);
delete from sprites where id='p1';