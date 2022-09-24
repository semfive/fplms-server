using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class SubjectRepository : RepositoryBase<Subject>, ISubjectRepository
    {
        public SubjectRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateSubject(Subject subject)
        {
            Create(subject);
        }

        public void DeleteSubject(Subject subject)
        {
            Delete(subject);
        }

        public async Task<IEnumerable<Subject>> GetAllSubjectsAsync()
        {
            return await FindAll().ToListAsync();
        }

        public async Task<Subject> GetSubjectByIdAsync(Guid subjectId)
        {
            return await FindByCondition(subject => subject.Id.Equals(subjectId)).FirstOrDefaultAsync();
        }

        public async Task<Subject> GetSubjectByNameAsync(string subjectName)
        {
            return await FindByCondition(subject => subject.Name.ToLower().Equals(subjectName.Trim().ToLower())).FirstOrDefaultAsync();
        }

        public void UpdateSubject(Subject subject)
        {
            Delete(subject);
        }
    }
}