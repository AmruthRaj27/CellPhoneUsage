# Cell Phone Usage Report

I have created this project which can be used to generate the cell phone
usage report as mentioned in the requirement.

### Assumptions
* One employee doesn't have more than one cell phones.
* In requirement, it was also mentioned that we need to generate minute and data usage
monthly report. Since there can be multiple years having same month in csv file,
I am just adding usage for all years having same month. This will restrict us
to have maximum 12 month columns.
* I have assumed that I can generate the report in any format. Therefore, I just
printed it on console in json format where mutiple records are separated by new line character.