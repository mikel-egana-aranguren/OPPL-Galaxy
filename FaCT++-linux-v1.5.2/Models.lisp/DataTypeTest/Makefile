# this is automatically generate makefile for KB

all: sat

clean:
	rm -f dl.res Taxonomy.Roles Taxonomy.log reasoning.log
	rm -f dl.res.diff Taxonomy.Roles.diff Taxonomy.log.diff reasoning.log.diff

KB_CONF = DataTypeTest.conf
KB = DataTypeTest.tbox

sat classify:
	FaCT++ $(KB_CONF)
