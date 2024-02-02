CREATE TABLE IF NOT EXISTS dico_recherche ( 
id INT NOT NULL AUTO_INCREMENT,  
domaine VARCHAR(50),
source VARCHAR(50),
liste_params LONGTEXT, 
liste_chemins LONGTEXT, 
PRIMARY KEY(id) 
)
;
CREATE TABLE IF NOT EXISTS dico_telechargement (
id INT NOT NULL AUTO_INCREMENT, 
domaine VARCHAR(50),
source VARCHAR(50),
liste_marks LONGTEXT,
PRIMARY KEY(id)
)
;
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (1,'dl.acm.org','ACM','query','/results.cfm');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (2,'www.aluka.org','Aluka','Query','/heritage/search;/struggles/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (3,'www.annualreviews.org','Annual Reviews','AllField;text#','/action/doSearch');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (4,'www.cairn.info','CAIRN','searchTerm;Word#','/resultats_recherche.php');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (5,'www.elgaronline.com','Elgar Online','q#;q_#','/noresults;/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (6,'ieeexplore.ieee.org','IEEE','queryText;searchWithin','/search/searchresult.jsp');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (7,'www.sciencedirect.com','Science direct','qs;authors;pub;cid;volume;issue;page;Sterm;query','/search;/science/related-books');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (8,'www.jstor.org','JSTOR','Query;q#','/action/doBasicSearch;/action/doAdvancedSearch');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (9,'plants.jstor.or','JSTOR Plants','Query;q#','/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (10,'iopscience.iop.org','IOP Science','fieldedquery;terms;','/search;/nsearch;/proquestcount');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (11,'materials.springer.com','Springer Materiels','searchTerm','/textsearch;/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (12,'www.oecd-ilibrary.org','OCED Library','value#;form_name','/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (13,'pubs.rsc.org','RSC Publishing Home','Searchtext;AllText;ExactText;AtleastText;WithoutText;AuthorGivenName#;AuthorFamilyName#','/en/result;/en/results/all');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (14,'link.springer.com','Springer Link','query;term','/search');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (15,'www.springerprotocols.com','Springer Protocols','Text;text;abstractText;title;author','/cdp/search/searchResultPage');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (16,'zbmath.org','zbMATH','q;f','/;/authors/;/journals/;/classification/ ');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (17,'www.clinicalkey.com','Clinical Key','','/#!/search/');
INSERT INTO `dico_recherche` (`id`,`domaine`,`source`,`liste_params`,`liste_chemins`) VALUES (18,'www.clinicalkey.fr','Clinical Key','','/#!/search/');
;
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (1,'portalparts.acm.org','ACM','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (2,'delivery.acm.org','ACM','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (3,'www.annualreviews.org','Annual Reviews','doi/pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (4,'www.cairn.info','CAIRN','load_pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (5,'www.elgaronline.com','Elgar Online','downloadpdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (6,'www.clinicalkey.fr','Clinical Key','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (7,'ieeexplore.ieee.org','IEEE','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (8,'ac.els-cdn.com','Science direct','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (9,'www.sciencedirect.com','Science direct','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (10,'www.jstor.org/','JSTOR','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (11,'plants.jstor.org','JSTOR Plants','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (12,'www.aluka.org','Aluka','pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (13,'iopscience.iop.org','IOP Science','/pdf;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (14,'www.oecd-ilibrary.org','OCED Library','.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (15,'www.pediatricneurologybriefs.com','Pediatric Neurology Briefs','download');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (16,'pubs.rsc.org','RSC Publishing Home','/articlepdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (17,'link.springer.com','Springer Link','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (18,'www.springerprotocols.com','Springer Protocols','/pdf/');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (19,'zbmath.org','zbMATH','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (20,'page-one.live.cf.public.springer.com','Springer Link','/pdf/;.pdf');
INSERT INTO `dico_telechargement` (`id`,`domaine`,`source`,`liste_marks`) VALUES (21,'www.clinicalkey.com','Clinical Key','/pdf/;.pdf');
